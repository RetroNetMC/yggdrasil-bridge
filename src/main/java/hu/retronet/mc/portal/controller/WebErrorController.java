package hu.retronet.mc.portal.controller;

import hu.retronet.mc.common.model.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Slf4j
@Controller

public class WebErrorController implements ErrorController {

    @RequestMapping("/error")
    public String post(HttpServletRequest request, Model model) {
        return handler(request, model);
    }

    private static @NonNull String handler(HttpServletRequest request, Model model) {
        String servletName = (String) request.getAttribute(RequestDispatcher.ERROR_SERVLET_NAME);
        if (servletName != null) {
            model.addAttribute("servlet_name", servletName);
        }
        ErrorResponse error;
        if (model.getAttribute("error") != null && model.getAttribute("error") instanceof ErrorResponse) {
            error = (ErrorResponse) model.getAttribute("error");
            Class<?> origin = (Class<?>) model.getAttribute("error_origin");
            assert origin != null;
            log.debug("[Transferred from '{}'] Error occurred: {} - {} at {}. Exception: {}", origin.getSimpleName(), error != null ? error.getStatusCode() : null,
                    error != null ? error.getCause() : null, error != null ? error.getPath() : null,
                    error != null ? error.getErrorMessage() : null);
        } else if (request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE) != null) {
            Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
            String message = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
            String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
            Throwable exception = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            LocalDateTime timestamp = LocalDateTime.now();
            String errorName;
            String cause;
            if(exception != null) {
                errorName = exception.getClass().getSimpleName();
                cause = exception.getCause() != null ? exception.getCause().getMessage() : null;
            } else {
                // TODO: Get error from status code
                errorName = "Unknown Error";
                cause = null;
            }
            error = new ErrorResponse(
                    errorName,
                    cause,
                    message,
                    status,
                    requestUri,
                    timestamp
            );
            log.debug("Error occurred: {} - {} at {}. Exception: {}", status, requestUri, errorName, cause);
        } else {
            log.debug("No error information found in request attributes. Maybe user navigated to /error directly. Redirecting to base url.");
            return "redirect:/";
        }
        model.addAttribute("error", error);
        request.getSession().invalidate();
        return "error";
    }

}
