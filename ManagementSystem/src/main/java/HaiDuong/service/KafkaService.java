package HaiDuong.service;

import HaiDuong.request.EmailRequest;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaService {
    @Autowired
    private EmailService emailService;

    @KafkaListener(id="emailGroup",topics = "email")
    void listenKafka(EmailRequest email) throws MessagingException {
        emailService.sendEmailAuthenticated(email);
    }

    @KafkaListener(id="subscriptionGroup",topics = "subscription")
    void subscriptionGroup(EmailRequest email) throws MessagingException {
        emailService.sendEmailSubscription(email);
    }
}
