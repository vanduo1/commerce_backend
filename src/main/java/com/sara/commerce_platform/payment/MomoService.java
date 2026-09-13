package com.sara.commerce_platform.payment;

import com.sara.commerce_platform.common.utils.HmacUtil;
import com.sara.commerce_platform.config.MomoConfig;
import com.sara.commerce_platform.order.entity.Order;
import com.sara.commerce_platform.order.entity.OrderStatus;
import com.sara.commerce_platform.order.repository.OrderRepository;
import com.sara.commerce_platform.payment.dto.MomoIpnRequest;
import com.sara.commerce_platform.payment.dto.MomoRequest;
import com.sara.commerce_platform.payment.dto.MomoResponse;
import com.sara.commerce_platform.payment.entity.Payment;
import com.sara.commerce_platform.payment.entity.PaymentStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@Slf4j
public class MomoService {

    private final MomoConfig momoConfig;
    private final RestTemplate restTemplate = new RestTemplate();
    private final OrderRepository orderRepository;


    public MomoService(MomoConfig momoConfig, OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.momoConfig = momoConfig;
        this.orderRepository = orderRepository;
    }

    public String createPaymentUrl(Order order) {
        String requestId = UUID.randomUUID().toString();

        String orderId = order.getId() + "_" + System.currentTimeMillis();
        String amount = String.valueOf(order.getTotalPrice().longValue());
        String orderInfo = "Order payment #" + order.getId();
        String requestType = "captureWallet";
        String extraData = "";

        String rawData = "accessKey=" + momoConfig.accessKey()
                + "&amount=" + amount
                + "&extraData=" + extraData
                + "&ipnUrl=" + momoConfig.notifyUrl()
                + "&orderId=" + orderId
                + "&orderInfo=" + orderInfo
                + "&partnerCode=" + momoConfig.partnerCode()
                + "&redirectUrl=" + momoConfig.returnUrl()
                + "&requestId=" + requestId
                + "&requestType=" + requestType;

        String signature = HmacUtil.calculateHMac(rawData, momoConfig.secretKey());

        MomoRequest requestBody = new MomoRequest(
                momoConfig.partnerCode(),
                "Commerce platform",
                "Store_1",
                requestId,
                amount,
                orderId,
                orderInfo,
                momoConfig.returnUrl(),
                momoConfig.notifyUrl(),
                "vi",
                extraData,
                requestType,
                signature
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MomoRequest> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<MomoResponse> response = restTemplate.postForEntity(momoConfig.endpoint(), entity, MomoResponse.class);
            MomoResponse body = response.getBody();

            if(body !=  null && body.payUrl() != null) {
                return body.payUrl();
            } else {
                log.error("Momo response: {}", response);
                throw new RuntimeException("Error creating Momo transaction");
            }
        } catch (Exception e) {
            log.error("Error connecting Momo API", e);
            throw new RuntimeException("Can not connect payment gateway");
        }
    }

    public void processIpn(MomoIpnRequest ipnData) {
        log.info("Receiving IPN from Momo: {}", ipnData);

        String providedSignature = ipnData.signature();

        String rawData = "accessKey=" + momoConfig.accessKey()
                + "&amount=" + ipnData.amount()
                + "&extraData=" + ipnData.extraData()
                + "&message=" + ipnData.message()
                + "&orderId=" + ipnData.orderId()
                + "&orderInfo=" + ipnData.orderInfo()
                + "&orderType=" + ipnData.orderType()
                + "&partnerCode=" + ipnData.partnerCode()
                + "&payType=" + ipnData.payType()
                + "&requestId=" + ipnData.requestId()
                + "&responseTime=" + ipnData.responseTime()
                + "&resultCode=" + ipnData.resultCode()
                + "&transId=" + ipnData.transId();

        String calculatedSignature = HmacUtil.calculateHMac(rawData, momoConfig.secretKey());

        if(!calculatedSignature.equals(providedSignature)) {
            log.error("Invalid IPN signature. Someone is impersonating Momo");
            return;
        }

        String rawOrderId = ipnData.orderId();
        String actualOrderId = rawOrderId.split("_")[0];
        int resultCode = ipnData.resultCode();

        Order order = orderRepository.findById(actualOrderId).orElseThrow(() -> new RuntimeException("Order not found: " + actualOrderId));


        if (order.getStatus() != OrderStatus.CREATED) {
            log.warn("Order {} has been processed", actualOrderId);
            return;
        }

        if(resultCode == 0) {
            order.setStatus(OrderStatus.PENDING);
            Payment payment = Payment.builder()
                    .status(PaymentStatus.SUCCESS)
                    .transactionId(String.valueOf(ipnData.transId()))
                    .build();
            order.getPayments().add(payment);
            log.info("Successful payment: {}", actualOrderId);

        } else {
            log.info("Failed payment: {}", actualOrderId);
        }

        orderRepository.save(order);
    }


}
