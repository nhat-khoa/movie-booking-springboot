package com.ticket.moviebooking.service;

import com.google.zxing.WriterException;
import com.ticket.moviebooking.util.QRCodeGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class MailService {

    JavaMailSender mailSender;

    public void sendBookingSuccessMail(String toEmail, String ticketId) throws MessagingException, IOException, WriterException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = getMimeMessageHelper(toEmail, ticketId, message);

        // Generate QR code
        byte[] qrCode = QRCodeGenerator.generateQRCodeImage(ticketId, 250, 250);
        ByteArrayResource qrAttachment = new ByteArrayResource(qrCode);

        // Gắn QR vào mail
        helper.addInline("qrCodeImage", qrAttachment, "image/png");

        mailSender.send(message);
    }

    private static MimeMessageHelper getMimeMessageHelper(String toEmail, String ticketId, MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject("🎉 Đặt chỗ thành công - Mã vé: " + ticketId);
        helper.setText(
                "<h3>Bạn đã đặt chỗ thành công!</h3>" +
                        "<p>Cảm ơn bạn đã sử dụng dịch vụ. Mã vé của bạn là: <b>" + ticketId + "</b></p>" +
                        "<p>Vui lòng xuất trình mã QR dưới đây khi đến điểm check-in:</p>" +
                        "<img src='cid:qrCodeImage'>",
                true
        );
        return helper;
    }
}

