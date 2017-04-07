package com.wdpr.payment.adminservice.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.wdpr.payment.data.vo.ErrorHolder;
import com.wdpr.payment.helper.template.FreemarkerTemplater;
import com.wdpr.payment.util.JavaUtil;
import com.wdpr.payment.util.PaymentConstants;
import com.wdpr.payment.util.PaymentLogger;

import dpr.disney.com.adaptivepayment.common.TxnApprovalStatusEnum;

public class MailHelper {

    private static final Logger PP_LOG = PaymentLogger.getCustomLogger(MailHelper.class);

    @Value("${mail_fromAddress}")
    private String fromAddress;

    @Value("${approver_email_template}")
    private String approverEmailTemplate;

    @Value("${submitter_email_template}")
    private String submitterEmailTemplate;
    
    @Value("${sendforreviewtext}")
    private String sendForReviewText;
    
    // values added for bulk load error email 
    
    @Value("${bulk_error_email_template}")
    private String bulkErrorEmailTemplate;
    
    @Value("${bulk_error_receipt_template}")
    private String bulkErrorReceiptTemplate;

    private JavaMailSender mailSender;
    private FreemarkerTemplater templater;
    private String text = PaymentConstants.EMPTY_STRING;

    public FreemarkerTemplater getTemplater() {
        return this.templater;
    }

    public void setTemplater(final FreemarkerTemplater templater) {
        this.templater = templater;
    }

    public void setMailSender(final JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Send email to the recipients as per the content from email template
     *
     * @param templateMap
     * @param toAddress
     * @param errors
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public void sendConfirmationEmail(final Map<String, Object> templateMap, final String toAddress[],
            final List<ErrorHolder> errors, final String subject) throws Exception {
        final String sendForReviewStatus = this.sendForReviewText;
        final String approvalStatus = null == templateMap.get(PaymentConstants.APPROVAL_STS_CD) ? null : templateMap
                .get(PaymentConstants.APPROVAL_STS_CD).toString();
        
        

            if (PaymentConstants.SUBMITTER == templateMap.get(PaymentConstants.ROLE_TYPE)) {
                this.text = this.getTemplater().evalTemplate(templateMap, errors, this.submitterEmailTemplate);
            } else if (PaymentConstants.APPROVER == templateMap.get(PaymentConstants.ROLE_TYPE)) {
                this.text = this.getTemplater().evalTemplate(templateMap, errors, this.approverEmailTemplate);
            }


        PP_LOG.warn("Email List :: " + Arrays.toString(toAddress));
        PP_LOG.warn("Template text :: " + this.text);

        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                String emailSubject;
                if (PaymentConstants.SUBMITTER == templateMap.get(PaymentConstants.ROLE_TYPE)) {
                    if (TxnApprovalStatusEnum.SEND_FOR_REVIEW.value().equals(
                            templateMap.get(PaymentConstants.APPROVAL_STS_CD))) {
                        emailSubject = addStatusInSubject(subject, sendForReviewStatus);
                    } else {
                        emailSubject = addStatusInSubject(subject, approvalStatus);
                    }
                } else {
                    emailSubject = subject;
                }
                message.setTo(toAddress);
                message.setFrom(MailHelper.this.fromAddress);
                message.setSubject(MimeUtility.encodeText(emailSubject, "cp1252", "Q"));
                message.setText(MailHelper.this.text, true);
            }
        };
        this.mailSender.send(preparator);
    }
    
    /**
     * Method send email in case of bulk file upload with error records as attachment. 
     * 
     * @param templateMap
     * @param toAddress
     * @param errors
     * @param subject
     */
    public void sendBulkErrorEmail(final Map<String, Object> templateMap, final String toAddress[],
            final List<ErrorHolder> errors, final String subject) {
        
        // Get the email content 
        String emailText= this.getTemplater().evalTemplate(templateMap, errors, this.bulkErrorEmailTemplate);
        PP_LOG.info("Template Text -- \n" + emailText);

        final MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(final MimeMessage mimeMessage) throws Exception {
                // try {
                final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
                final String subject = "[Payment Administration Portal] : ERROR - Action Required : File - " + templateMap.get("fileName");
                PP_LOG.info("Email Subject -- : " + subject);

                message.setTo(toAddress);
                message.setFrom(MailHelper.this.fromAddress);
                message.setSubject(MimeUtility.encodeText(subject, "cp1252", "Q"));
                message.setText(emailText, true);
                String attachmentText = constructReceipt(templateMap, errors);
                PP_LOG.info("AttachmentText Template -- \n" + attachmentText);
                if (!JavaUtil.isNullOrEmpty(attachmentText)) {
                    message.addAttachment("bulk_file_errors.html", new ByteArrayResource(attachmentText.getBytes()));
                }
            }
        };
        this.mailSender.send(preparator);
        
    }

    static String addStatusInSubject(final String content, final String status) {
        if (content.contains(PaymentConstants.APPROVAL_STS_CD)) {
            return content.replace(PaymentConstants.APPROVAL_STS_CD, status);
        }
        return content;
    }
    
    private String constructReceipt(final Map<String, Object> templateMap,final List<ErrorHolder> errors) {
        // build receipt, will generate only for success txns
        
        final StringBuilder receipt = new StringBuilder();
        try {
            receipt.append(this.templater.evalTemplate(templateMap,errors, this.bulkErrorReceiptTemplate));
          } catch (final Exception ex) {
            PP_LOG.error("Unable to create receipt..." + ex.getMessage());
            return "Unable to create receipt. Please notify the APP Support Team";
        }
        return receipt.toString();
    }
}
