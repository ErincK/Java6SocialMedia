package com.erinc.manager;

import com.erinc.dto.request.UpdateEmailOrUsernameRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import static com.erinc.constant.ApiUrls.FINDBYROLE;

@FeignClient(name = "userprofile-auth",url ="http://localhost:7071/api/v1/auth",decode404 = true )
public interface IAuthManager {


    @PutMapping("/updateemailorusername")
    public ResponseEntity<Boolean> updateEmailOrUsername(@RequestBody UpdateEmailOrUsernameRequestDto dto);


    @GetMapping(FINDBYROLE)
    public ResponseEntity<List<Long>> findByRole(@RequestParam String role);

}
