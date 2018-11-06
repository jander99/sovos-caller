package com.homedepot.fi.tx.sovoscaller.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.ws.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SovosCaller {

    @Autowired
    WebClient webClient;

    @Autowired
    RestTemplate restTemplate;

    private String payload = "{\n"
        + "  \"usrname\": \"restprod@HOMEDEPOT\",\n"
        + "  \"pswrd\": \"Taxware123\",\n"
        + "  \"isAudit\": \"false\",\n"
        + "  \"rsltLvl\": \"3\",\n"
        + "  \"tdmRequired\": \"false\",\n"
        + "  \"tdcReqrd\": \"false\",\n"
        + "  \"currn\": \"USD\",\n"
        + "  \"txCalcTp\": \"1\",\n"
        + "  \"docDt\": \"2018-06-01\",\n"
        + "  \"trnDocNum\": \"PROD\",\n"
        + "  \"lines\": [{\n"
        + "    \"debCredIndr\": \"1\",\n"
        + "    \"lnItmId\": \"test\",\n"
        + "    \"qnty\": \"1\",\n"
        + "    \"trnTp\": \"1\",\n"
        + "    \"grossAmt\": \"100\",\n"
        + "    \"orgCd\": \"THD-1001\",\n"
        + "    \"dlvrAmt\": \"10\",\n"
        + "    \"goodSrvCd\": \"test code\",\n"
        + "    \"myDlvrTermsCode\": \"ABC\",\n"
        + "    \"myMTCd\": \"1\",\n"
        + "    \"txAmt\": \"10.25\",\n"
        + "    \"lOAGeoCd\": \"2753\",\n"
        + "    \"lORGeoCd\": \"2753\",\n"
        + "    \"lUGeoCd\": \"2753\",\n"
        + "    \"sFGeoCd\": \"2753\",\n"
        + "    \"sTLocCd\": \"245\",\n"
        + "    \"sTStateProv\": \"MA\",\n"
        + "    \"sTCountry\": \"US\"\n"
        + "  }]\n"
        + "}";

    @Value("${sovos.username}")
    private String userName;

    @Value("${sovos.key}")
    private String key;
    //private String key = "3b9f55c6-086c-4676-80a2-948700401c10";

    @Value("${sovos.url}")
    private String sovosUrl;


    public Mono<String> callSovosWebClient() {

        try {
            log.info("Username: " + userName);
            log.info("Sovos Key: " + key);
            log.info("Sovos URL: " + sovosUrl);

            String currentDate = getCurrentDate();

            String hmac = concatHMAC(generateHMAC(key, currentDate));

            log.info("HMAC: " + hmac);
            log.info("Date: " + currentDate);
            log.info("JSON: " + payload);

            return webClient.post()
                .uri(sovosUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", hmac)
                .header("Date", currentDate)
                .syncBody(payload)
                .retrieve()
                .bodyToMono(String.class);
        } catch(Exception e) {
            return Mono.just("Help");
        }
    }

    public ResponseEntity<String> callSovosRestTemplate() throws Exception {

        log.info("Username: " + userName);
        log.info("Sovos Key: " + key);
        log.info("Sovos URL: " + sovosUrl);

        String currentDate = getCurrentDate();

        String hmac = concatHMAC(generateHMAC(key, currentDate));

        log.info("HMAC: " + hmac);
        log.info("Date: " + currentDate);
        log.info("JSON: " + payload);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", hmac);
        headers.add("Date", currentDate);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        String request = objectMapper.writeValueAsString(payload);
        HttpEntity<String> httpEntity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(sovosUrl, HttpMethod.POST, httpEntity, String.class);
        } catch(Exception e) {
            log.error("Unable to make call to Sovos.", e);
        }

        return ResponseEntity.status(418).body("Oops");

    }

    public String concatHMAC(String hmac) {

        return concatString("TAX "
            , userName
            , ":"
            , hmac);

    }

    public String generateHMAC(String key, String date) throws Exception {

        String hmacSignature = null;
        String restUrlResource = concatString(
            "/Twe/api/rest/",
            "calcTax/",
            "doc",
            userName,
            "Taxware123");

        String hmacFinalMessage = concatString(HttpMethod.POST, MediaType.APPLICATION_JSON_VALUE, date, restUrlResource);
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(hmacFinalMessage.getBytes());
        hmacSignature = javax.xml.bind.DatatypeConverter.printBase64Binary(rawHmac);

        return hmacSignature;
    }

    public String concatString(Object... args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object arg : args) {
            stringBuilder.append(arg);
        }
        return stringBuilder.toString();
    }

    public String getCurrentDate() {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        dateFormat.setTimeZone(timeZone);
        String currentDate = dateFormat.format(new Date());
        return currentDate;
    }

}
