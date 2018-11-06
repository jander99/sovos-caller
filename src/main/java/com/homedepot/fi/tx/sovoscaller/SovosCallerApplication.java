package com.homedepot.fi.tx.sovoscaller;

import com.homedepot.fi.tx.sovoscaller.services.SovosCaller;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@Slf4j
public class SovosCallerApplication /*implements CommandLineRunner*/ {

    @Autowired
    SovosCaller sovosCaller;

    public static void main(String[] args) {
        SpringApplication.run(SovosCallerApplication.class, args);
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {


//        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
//        Proxy proxy = new Proxy(
//            Type.HTTP, new InetSocketAddress("thd-svr-proxy-qa.homedepot.com", 7070));
//        factory.setProxy(proxy);
//
//        return new RestTemplate(factory);


        return new RestTemplate();



//        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
//
//        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
//            .loadTrustMaterial(null, acceptingTrustStrategy)
//            .build();
//
//        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
//
//        CloseableHttpClient httpClient = HttpClients.custom()
//            .setSSLSocketFactory(csf)
//            .build();
//
//        HttpComponentsClientHttpRequestFactory requestFactory =
//            new HttpComponentsClientHttpRequestFactory();
//
//        requestFactory.setHttpClient(httpClient);
//        RestTemplate restTemplate = new RestTemplate(requestFactory);
//        return restTemplate;
    }
}
