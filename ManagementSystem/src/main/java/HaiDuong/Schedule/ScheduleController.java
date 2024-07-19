package HaiDuong.Schedule;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
public class ScheduleController {

    @GetMapping()
    public ResponseEntity<?> schedule() {
        return new ResponseEntity<>("Hello World", HttpStatus.OK);
    }
}
