package yeoksamstationexit1.recommend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);

    public ResponseEntity<String> errorMessage(Exception e) {
        logger.error("An error occurred: " + Arrays.toString(e.getStackTrace()));
        return new ResponseEntity<>("에러 메세지: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
