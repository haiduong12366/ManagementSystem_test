package HaiDuong.service;

import HaiDuong.Model.PlanType;
import HaiDuong.Model.Subscription;
import HaiDuong.Model.User;
import HaiDuong.repository.SubscriptionRepository;
import HaiDuong.request.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SubscriptionServiceImpl implements SubscriptionService{
    @Autowired
    private UserService userService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; //key string, value object bc of JsonSerializer in application


    @Override
    public Subscription createSubscription(User user) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setSubscriptionStartDate(LocalDate.now());
        subscription.setGetSubscriptionEndDate(LocalDate.now().plusMonths(12));
        subscription.setValid(true);
        subscription.setPlanType(PlanType.FREE);

        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription getUserSubscription(Long userId) throws Exception {
        Subscription subscription = subscriptionRepository.findByUserId(userId);
        if(!isValid(subscription)){
            subscription.setPlanType(PlanType.FREE);
            subscription.setGetSubscriptionEndDate(LocalDate.now().plusMonths(12));
            subscription.setSubscriptionStartDate(LocalDate.now());
        }
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription upgradeSubscription(Long userId, PlanType planType) throws Exception {
        Subscription subscription = subscriptionRepository.findByUserId(userId);
        subscription.setPlanType(planType);
        subscription.setSubscriptionStartDate(LocalDate.now());
        User u = userService.findUserById(userId);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(u.getEmail());
        emailRequest.setName(u.getFullName());
        emailRequest.setSubject("Payment Confirmation");

        if(planType.equals(PlanType.ANNUALLY)){
            subscription.setGetSubscriptionEndDate(LocalDate.now().plusMonths(12));
            emailRequest.setPlanType("ANNUALLY");

        }else {
            subscription.setGetSubscriptionEndDate(LocalDate.now().plusMonths(1));
            emailRequest.setPlanType("MONTHLY");
        }
        kafkaTemplate.send("subscription", emailRequest);
        return subscriptionRepository.save(subscription);
    }

    @Override
    public boolean isValid(Subscription subscription) {
        if(subscription.getPlanType().equals(PlanType.FREE))
            return true;
        LocalDate endDate = subscription.getGetSubscriptionEndDate();
        LocalDate currentDate = LocalDate.now();
        return endDate.isAfter(currentDate) || endDate.isEqual(currentDate);
    }
}
