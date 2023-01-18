package com.smirnov.app.web;

import com.smirnov.app.web.advices.dto.CommonErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<CommonErrorResponse> handleError(HttpServletRequest request) {

        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(new CommonErrorResponse(
                                HttpStatus.NOT_FOUND.value(),
                                "not found"
                        ));
            }
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new CommonErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "internal service error"
                ));

    }
}
