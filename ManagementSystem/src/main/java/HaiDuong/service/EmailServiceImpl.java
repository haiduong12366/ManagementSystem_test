package HaiDuong.service;

import HaiDuong.request.EmailRequest;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;


@Service
public class EmailServiceImpl implements EmailService{

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired
    private SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmailWithToken(String userEmail, String link) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");

        String subject = "Join project team Invitation";
        String text = "Click the link to join the project team: " + link;
        helper.setSubject(subject);
        helper.setText(text,true);
        helper.setTo(userEmail);
        try{
            javaMailSender.send(mimeMessage);
        }catch (MailSendException e){
            throw new MailSendException("Failed to send email");
        }
    }

    @Override
    @Async
    public void sendEmailAuthenticated(EmailRequest email) throws MessagingException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");
            Context context = new Context();
            context.setVariable("content",email.getMessage());
            String html = templateEngine.process("welcome-mail", context);
            helper.setSubject(email.getSubject());
            helper.setText(html,true);
            helper.setTo(email.getEmail());
            javaMailSender.send(mimeMessage);

        }catch (MessagingException e){
            throw new MessagingException("Failed to send email");
        }
    }

    @Override
    public void sendEmailSubscription(EmailRequest email) throws MessagingException {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,"utf-8");
            Context context = new Context();
            context.setVariable("name",email.getName());
            context.setVariable("planType",email.getPlanType());
            context.setVariable("email",email.getEmail());
            String html = templateEngine.process("SubscriptionMail", context);
            helper.setSubject(email.getSubject());
            helper.setText(html,true);
            helper.setTo(email.getEmail());
            javaMailSender.send(mimeMessage);

        }catch (MessagingException e){
            throw new MessagingException("Failed to send email");
        }
    }


}
