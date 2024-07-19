package HaiDuong.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequest {
    private String email;
    private String message;
    private String subject;
    private String planType;
    private String name;
}
