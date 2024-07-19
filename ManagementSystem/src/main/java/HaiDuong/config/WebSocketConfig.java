package HaiDuong.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(10000000); // default : 64 * 1024
        registry.setSendTimeLimit(20 * 10000); // default : 10 * 10000
        registry.setSendBufferSizeLimit(3* 512 * 1024); // default : 512 * 1024.

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins(
                "https://haiduong12366.github.io/",
                "https://main--haiduong.netlify.app/",
                "http://localhost:5173/",
                "http://localhost:4200/",
                "http://localhost:4173/"
                ).withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/group","/user");
        registry.setUserDestinationPrefix("/group");
    }
}
