package com.UrlShortner.UrlShortner.Services;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.UrlShortner.UrlShortner.Model.Url;
import com.UrlShortner.UrlShortner.Model.UrlDto;
import com.UrlShortner.UrlShortner.Repository.UrlRepository;
import com.google.common.hash.Hashing;

import io.micrometer.common.util.StringUtils;

@Component
public class UrlServiceImpl implements UrlService{
    
    @Autowired
     private UrlRepository urlRepository;

    @Override
    public Url generateShortLink(UrlDto urlDto) {
        if (StringUtils.isNotEmpty(urlDto.getUrl())) {
            String encodedUrl=encodeUrl(urlDto.getUrl());
            Url urlToPersist=new Url();
            urlToPersist.setCreationDate(LocalDateTime.now());
            urlToPersist.setOriginalUrl(urlDto.getUrl());
            urlToPersist.setShortLink(encodedUrl);
            urlToPersist.setExpirationDate(getExpirationDate(urlDto.getExpirationDate(),urlToPersist.getCreationDate()));
            Url urlToRet = persistShortLink(urlToPersist);

            if(urlToRet != null)
                return urlToRet;

            return null;
        }
        return null;
       
    }

    private LocalDateTime getExpirationDate(String expirationDate, LocalDateTime creationDate) {
        if(StringUtils.isBlank(expirationDate))
        {
            return creationDate.plusSeconds(60);
        }
        LocalDateTime expirationDateToRet = LocalDateTime.parse(expirationDate);
        return expirationDateToRet;
        
    }

    private String encodeUrl(String url) {
        String encodedUrl="";
        LocalDateTime time=LocalDateTime.now();
        encodedUrl=Hashing.murmur3_32().hashString(url.concat(time.toString()),StandardCharsets.UTF_8).toString();
        return encodedUrl;
    }

    @Override
    public Url persistShortLink(Url url) {
        Url urlToRet=urlRepository.save(url);
        return urlToRet;
    }

    @Override
    public Url getEncodedUrl(String url) {
        Url urlToRet=urlRepository.findByShortLink(url);
        return urlToRet;
    }

    @Override
    public void deleteShortLink(Url url) {
        urlRepository.delete(url);
    }

}
