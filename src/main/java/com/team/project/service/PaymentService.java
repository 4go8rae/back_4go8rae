package com.team.project.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.project.domain.CartItem;
import com.team.project.domain.Member;
import com.team.project.domain.Payment;
import com.team.project.domain.Product;
import com.team.project.dto.request.PaymentConfirmRequestDto;
import com.team.project.dto.request.PaymentRequestDto;
import com.team.project.dto.response.PaymentDetailResponseDto;
import com.team.project.dto.response.PaymentResponseDto;
import com.team.project.exception.CustomException;
import com.team.project.exception.ErrorCode;
import com.team.project.jwt.UserDetailsImpl;
import com.team.project.repository.CartItemRepository;
import com.team.project.repository.MemberRepository;
import com.team.project.repository.PaymentRepository;
import com.team.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;

    @Value("${iamport.key}")
    private String key;

    @Value("${iamport.secret}")
    private String secret;


    @Transactional
    public ResponseEntity<PaymentResponseDto> payment(List<PaymentRequestDto> requestDtos, UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        int total = 0;
        String title = "";
        Payment payment;
        Long id = System.currentTimeMillis();

        PaymentRequestDto requestDto = requestDtos.get(0);
        Product product = (productRepository.findById(requestDto.getProduct_id()).orElse(null));

        if (product == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        title = product.getTitle();
        total += requestDto.getCount() * product.getPrice();

        if (requestDto.getCart_id() != null) {

            for (int i = 1; i < requestDtos.size(); i++) {
                PaymentRequestDto paymentRequestDto = requestDtos.get(i);
                product = (productRepository.findById(paymentRequestDto.getProduct_id()).orElse(null));
                if (product == null)
                    throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);
                total += paymentRequestDto.getCount() * product.getPrice();

                CartItem cartItem = cartItemRepository.findById(paymentRequestDto.getCart_id()).orElse(null);
                if (cartItem == null)
                    throw new CustomException(ErrorCode.NOT_FOUND_ITEM);

                cartItem.setPayment_id(id);
            }
            ;

            if (requestDtos.size() > 1)
                title += " 외 " + (requestDtos.size() - 1);

        }

        payment = Payment.builder()
                .id(id)
                .title(title)
                .price(total)
                .member(member)
                .build();

        paymentRepository.save(payment);


        return ResponseEntity.ok(PaymentResponseDto.builder()
                .merchant_uid(payment.getId())
                .title(payment.getTitle())
                .price(payment.getPrice())
                .build());
    }


    public ResponseEntity<Boolean> paymentConfirm(PaymentConfirmRequestDto requestDtos) {
        Payment payment = paymentRepository.findById(requestDtos.getMerchant_uid()).orElse(null);

        if (payment == null)
            throw new CustomException(ErrorCode.NOT_FOUND_PRODUCT);

        return ResponseEntity.ok(payment.getPrice() == requestDtos.getPrice());
    }


    public ResponseEntity<List<PaymentResponseDto>> getPaymentList(UserDetailsImpl userDetails) {

        Member member = memberRepository.findByUsername(userDetails.getUsername()).orElse(null);
        if (member == null)
            throw new CustomException(ErrorCode.UNAUTHORIZED_LOGIN);

        List<Payment> paymentList = paymentRepository.findAllByMember(member);
        List<PaymentResponseDto> responseDtos = new ArrayList<>();

        for (Payment payment : paymentList) {
            responseDtos.add(PaymentResponseDto.builder()
                    .merchant_uid(payment.getId())
                    .title(payment.getTitle())
                    .price(payment.getPrice())
                    .build());
        }

        return ResponseEntity.ok(responseDtos);
    }


    public ResponseEntity<PaymentDetailResponseDto> getpayment(String id) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String token = getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        //HTTP 요청 보내기
        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/"+id,
                HttpMethod.GET,
                request,
                String.class
        );
        //HTTP 응답 (JSON) -> 액세스 토큰 파싱
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode result = objectMapper.readTree(jsonNode.get("response").toString());

        Date date = new Date(result.get("paid_at").asLong());
        System.out.println(date.getTime());

        return ResponseEntity.ok(PaymentDetailResponseDto.builder()
                .merchant_uid(result.get("merchant_uid").asLong())
                .name(result.get("name").asText())
                .paid_at("201814109")
                .amount(result.get("amount").asInt())
                .buyer_name(result.get("buyer_name").asText())
                .buyer_tel(result.get("buyer_tel").asText())
                .buyer_email(result.get("buyer_email").asText())
                .buyer_addr(result.get("buyer_addr").asText())
                .pay_method(result.get("pay_method").asText())
                .emb_pg_provider(result.get("emb_pg_provider").asText())
                .card_name(result.get("card_name").asText())
                .card_number(result.get("card_number").asText())
                .build());
    }


    public String getToken() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        //HTTP Body 생성
        JSONObject body = new JSONObject();
        body.put("imp_key", key);
        body.put("imp_secret", secret);


        //HTTP 요청 보내기
        HttpEntity<JSONObject> kakaoTokenRequest = new HttpEntity<>(body, headers);
        ResponseEntity<JSONObject> response = restTemplate.postForEntity("https://api.iamport.kr/users/getToken",kakaoTokenRequest,JSONObject.class);
        //HTTP 응답 (JSON) -> 액세스 토큰 파싱
        //JSON -> JsonNode 객체로 변환
        String responseBody = response.getBody().toString();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode result = objectMapper.readTree(jsonNode.get("response").toString());
        return result.get("access_token").asText();
    }
}









