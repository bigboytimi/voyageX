package com.bigboytimi.authenticationservice.event.listener;

import com.bigboytimi.authenticationservice.event.publisher.RegistrationCompleteEvent;
import com.bigboytimi.authenticationservice.model.User;
import com.bigboytimi.authenticationservice.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private User savedUser;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        /*
        Get the registered user
         */

        savedUser = event.getUser();
        /*
        Generate a token
         */
        String verificationToken = UUID.randomUUID().toString();
        /*
        Save the verification token to the user entity
         */
        userService.saveVerificationToken(savedUser, verificationToken);
        /*
        Build verification url for the user
         */
        String appUrl = event.getApplicationUrl()+"/api/v1/auth/verify-email?token="+verificationToken;
        /*
        Send the token to user email
         */
        try {
            sendVerificationEmail(appUrl);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Verify Your Email";
        String senderName = "VoyageX";
        String content = "<p> Hi, "+ savedUser.getFirstName()+", </p>"+
                "<p>Thank you for registering with us,"+""+
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\""+ url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";

        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);

        messageHelper.setFrom("timiolowookere79@gmail.com", senderName);
        messageHelper.setTo(savedUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(content, true);
        mailSender.send(message);
    }

}
