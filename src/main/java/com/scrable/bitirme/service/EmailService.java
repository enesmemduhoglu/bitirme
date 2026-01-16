package com.scrable.bitirme.service;

import com.scrable.bitirme.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final FileStorageService fileStorageService;

    public void sendVerificationEmail(User user) {
        String subject = "Email Verification";
        String verificationUrl = "http://localhost:8080/verify?code=" + user.getVerificationCode();
        String message = "Please verify your email by clicking the following link: " + verificationUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject(subject);
        email.setText(message);

        mailSender.send(email);
    }

    public void sendOrderConfirmationEmail(String to, com.scrable.bitirme.model.Order order) {
        String subject = "Siparişiniz #" + order.getId() + " alındı. Teşekkür ederiz, " + order.getUser().getFirstName()
                + "!";

        StringBuilder itemsHtml = new StringBuilder();
        for (com.scrable.bitirme.model.OrderItem item : order.getOrderItems()) {
            String imageKey = item.getProduct().getProductImage();
            String imageUrl = "https://via.placeholder.com/80"; // Default fallback

            if (imageKey != null && !imageKey.isBlank()) {
                if (imageKey.startsWith("http")) {
                    imageUrl = imageKey;
                } else {
                    // Use presigned URL for private S3 access
                    String presignedUrl = fileStorageService.generatePresignedUrlForEmail(imageKey);
                    if (presignedUrl != null) {
                        imageUrl = presignedUrl;
                    }
                }
            }

            java.math.BigDecimal lineTotal = item.getPriceAtPurchase()
                    .multiply(java.math.BigDecimal.valueOf(item.getQuantity()));

            itemsHtml.append("<tr>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; vertical-align: top; width: 100px;'>")
                    .append("<img src='").append(imageUrl)
                    .append("' alt='Ürün' style='width: 80px; height: auto; border: 1px solid #eee; border-radius: 4px; display: block;'>")
                    .append("</td>")
                    .append("<td style='padding: 10px; border-bottom: 1px solid #eee; vertical-align: top;'>")
                    .append("<div style='font-size: 14px; color: #007185; text-decoration: none; margin-bottom: 4px;'>")
                    .append(item.getProduct().getProductName()).append("</div>")
                    .append("<div style='font-size: 12px; color: #565959;'>Miktar: ").append(item.getQuantity())
                    .append("</div>")
                    .append("<div style='font-size: 16px; font-weight: bold; color: #B12704; margin-top: 4px;'>")
                    .append(lineTotal).append("₺</div>")
                    .append("</td>")
                    .append("</tr>");
        }

        String htmlMessage = "<!DOCTYPE html>" +
                "<html>" +
                "<body style='font-family: \"Segoe UI\", Helvetica, Arial, sans-serif; margin: 0; padding: 0; color: #333333; background-color: #ffffff;'>"
                +
                "<div style='max-width: 600px; margin: 0 auto;'>" +
                // Dark Header
                "<div style='background-color: #232f3e; padding: 15px; text-align: center; color: white; font-size: 14px; font-weight: bold;'>"
                +
                "<a href='http://localhost:3000/orders' style='color: white; text-decoration: none; margin: 0 15px;'>Siparişlerim</a>"
                +
                "<a href='http://localhost:3000/profile' style='color: white; text-decoration: none; margin: 0 15px;'>Hesabım</a>"
                +
                "<a href='http://localhost:3000' style='color: white; text-decoration: none; margin: 0 15px;'>Tekrar Satın Alın</a>"
                +
                "</div>" +

                // Greeting
                "<div style='text-align: center; padding: 20px 10px; border-bottom: 1px solid #eaeaea;'>" +
                "<h1 style='color: #000; font-size: 20px; font-weight: normal; margin: 0;'>Siparişiniz için teşekkür ederiz, <span style='font-weight: bold;'>"
                + order.getUser().getFirstName().toUpperCase() + "</span>!</h1>" +
                "</div>" +

                // Status Bar - Refined Matching Reference
                "<div style='padding: 20px 10px; text-align: center;'>" +
                "<table style='width: 100%; border-collapse: collapse; table-layout: fixed;'>" +
                "<tr>" +
                // Step 1: Sipariş Edildi (Active, Teal)
                "<td style='text-align: center; vertical-align: top; position: relative; width: 25%;'>" +
                "<div style='color: #0076ad; font-size: 24px; line-height: 1; margin-bottom: 5px; position: relative; z-index: 2;'>&#10003;</div>"
                + // Checkmark
                "<div style='position: absolute; top: 12px; left: 50%; width: 100%; height: 3px; background-color: #0076ad; z-index: 1;'></div>"
                + // Line to right (Teal)
                "<div style='font-size: 13px; font-weight: bold; color: #000;'>Sipariş edildi</div>" +
                "</td>" +

                // Step 2: Kargoya Verildi (Inactive, Gray)
                "<td style='text-align: center; vertical-align: top; position: relative; width: 25%;'>" +
                "<div style='width: 14px; height: 14px; background-color: #e0e0e0; border-radius: 50%; margin: 6px auto 10px auto; position: relative; z-index: 2;'></div>"
                + // Dot
                "<div style='position: absolute; top: 12px; left: -50%; width: 100%; height: 3px; background-color: #e0e0e0; z-index: 1;'></div>"
                + // Line from left (Gray)
                "<div style='position: absolute; top: 12px; left: 50%; width: 100%; height: 3px; background-color: #e0e0e0; z-index: 1;'></div>"
                + // Line to right (Gray)
                "<div style='font-size: 13px; color: #555;'>Kargoya verildi</div>" +
                "</td>" +

                // Step 3: Dağıtıma Çıktı (Inactive, Gray)
                "<td style='text-align: center; vertical-align: top; position: relative; width: 25%;'>" +
                "<div style='width: 14px; height: 14px; background-color: #e0e0e0; border-radius: 50%; margin: 6px auto 10px auto; position: relative; z-index: 2;'></div>"
                + // Dot
                "<div style='position: absolute; top: 12px; left: -50%; width: 100%; height: 3px; background-color: #e0e0e0; z-index: 1;'></div>"
                + // Line from left (Gray)
                "<div style='position: absolute; top: 12px; left: 50%; width: 100%; height: 3px; background-color: #e0e0e0; z-index: 1;'></div>"
                + // Line to right (Gray)
                "<div style='font-size: 13px; color: #555;'>Teslimat için<br>dağıtıma çıktı</div>" +
                "</td>" +

                // Step 4: Teslim Edildi (Inactive, Gray)
                "<td style='text-align: center; vertical-align: top; position: relative; width: 25%;'>" +
                "<div style='width: 14px; height: 14px; background-color: #e0e0e0; border-radius: 50%; margin: 6px auto 10px auto; position: relative; z-index: 2;'></div>"
                + // Dot
                "<div style='position: absolute; top: 12px; left: -50%; width: 100%; height: 3px; background-color: #e0e0e0; z-index: 1;'></div>"
                + // Back Line only
                "<div style='font-size: 13px; color: #555;'>Teslim edildi</div>" +
                "</td>" +
                "</tr>" +
                "</table>" +
                "</div>" +

                "<hr style='border: 0; border-top: 1px solid #eaeaea; margin: 0 20px;' />" +

                // Delivery Estimate & User Info
                "<div style='padding: 20px;'>" +
                "<div style='font-size: 14px; font-weight: bold; color: #333;'>" + order.getUser().getFirstName()
                + " - " + order.getShippingAddress().getCity() + "</div>" +
                "<div style='font-size: 14px; color: #555; margin-top: 4px;'>Sipariş Numarası " + order.getId()
                + "</div>" +
                "</div>" +

                // Action Button
                "<div style='padding: 0 20px 20px 20px;'>" +
                "<a href='http://localhost:3000/orders/" + order.getId()
                + "' style='display: inline-block; background-color: #FFD814; border: 1px solid #FCD200; border-radius: 20px; padding: 10px 30px; color: #000; text-decoration: none; font-size: 14px; font-weight: 500;'>Siparişi görüntüle veya düzenle</a>"
                +
                "</div>" +

                // Items Table
                "<div style='padding: 0 20px 20px 20px;'>" +
                "<table style='width: 100%; border-collapse: collapse;'>" +
                itemsHtml.toString() +
                "</table>" +
                "</div>" +

                // Total (Switched to TABLE)
                "<div style='padding: 20px; border-top: 1px solid #eaeaea;'>" +
                "<table style='width: 100%; border-collapse: collapse;'>" +
                "<tr>" +
                "<td style='font-size: 16px; color: #333; text-align: left;'>Toplam</td>" +
                "<td style='font-size: 18px; font-weight: bold; color: #B12704; text-align: right;'>"
                + order.getTotalAmount() + "₺</td>"
                +
                "</tr>" +
                "</table>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlMessage, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Mail gönderilirken hata oluştu", e);
        }
    }
}
