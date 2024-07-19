package HaiDuong.controller;

import HaiDuong.Model.User;
import HaiDuong.config.JwtProvider;
import HaiDuong.repository.UserRepository;
import HaiDuong.request.EmailRequest;
import HaiDuong.request.LoginRequest;
import HaiDuong.response.AuthResponse;
import HaiDuong.service.CustomeUserDetailsImpl;
import HaiDuong.service.SubscriptionService;
import HaiDuong.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomeUserDetailsImpl customeUserDetails;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate; //key string, value object bc of JsonSerializer in application

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> createUserHandler(@RequestBody User user) throws Exception {
        User isUserExist = userRepository.findByEmail(user.getEmail());
        if(isUserExist!=null){
            throw new Exception("email already exist with another account");
        }
        User createUser = new User();
        createUser.setPassword(passwordEncoder.encode(user.getPassword()));
        createUser.setEmail(user.getEmail());
        createUser.setFullName(user.getFullName());

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(user.getEmail());
        emailRequest.setSubject("New register");
        emailRequest.setMessage("You recently signed up new account on ManagementSystemWeb with this email. Doesn't you?");
        kafkaTemplate.send("email", emailRequest);
        User savedUser = userRepository.save(createUser);

        subscriptionService.createSubscription(savedUser);
        Authentication authentication = new  UsernamePasswordAuthenticationToken(savedUser.getEmail(),savedUser.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);

        AuthResponse res = new AuthResponse();
        res.setMessage("Signup success");
        res.setJwt(jwt);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody LoginRequest loginRequest) throws Exception {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Authentication authentication = authenticate(email,password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = JwtProvider.generateToken(authentication);


        User user = userService.findUserProfileByJwt("Bearer "+jwt);

        EmailRequest emailRequest = new EmailRequest();
        emailRequest.setEmail(user.getEmail());
        emailRequest.setSubject("New login");
        emailRequest.setMessage("You recently logged in with new device!");
        kafkaTemplate.send("email", emailRequest);

        AuthResponse res = new AuthResponse();
        res.setMessage("Login success");
        res.setJwt(jwt);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customeUserDetails.loadUserByUsername(email);
        if(userDetails==null){
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid username or password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
    }
}
