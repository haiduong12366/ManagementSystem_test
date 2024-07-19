package HaiDuong.service;


import HaiDuong.request.EmailRequest;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailWithToken(String userEmail,String Link) throws MessagingException;
    void sendEmailAuthenticated(EmailRequest email) throws MessagingException;
    void sendEmailSubscription(EmailRequest email) throws MessagingException;
}
