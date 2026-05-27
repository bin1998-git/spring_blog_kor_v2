package com.tenco.blog.payment;

import com.tenco.blog._core.errors.Exception404;
import com.tenco.blog.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${portone.store-id}")
    private String storeId;

    @Value("${portone.channel-key}")
    private String channelKey;
    @Value("${portone.api-secret}")
    private String apiSecret;

    /**
     *
     * 결제 사전 요청 생성
     * 프론트엔드가 결제창을 띄우기 전애ㅔ, 서버로 부터 교유한 결제 건 식별자를
     * 서버측에서 생성해서 발급 시켜준다
     * <p>
     * - 중복 결제 방지 (paymentId 유니크 설정 함)
     * - 위변조 방지 (paymentId 서버측에서 생성해서 내려 줌)
     *
     */

    public PaymentResponse.PrePareDTO 결제요청생성(Integer userId, Integer amount) {

        // 1. 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new Exception404("사용자를 찾을 수 없습니다");
        }

        // 2-1. paymentId 생성
        String paymentId = generatePaymentId(userId);

        // 2-2. 중복 방지 확인
        // 2-3 만약 중복 발생했다면? 다시주문번호 생성 -> 다시확인
        while (paymentRepository.existByPaymentId(paymentId)) {
            paymentId = generatePaymentId(userId);
        }

        return new PaymentResponse.PrePareDTO(paymentId, amount, storeId, channelKey);
    }

    private String generatePaymentId(Integer userId) {
        return "point_" + userId + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
}
