package com.example.group_investment.auth;

import com.example.common.dto.ApiResponse;
import com.example.group_investment.auth.dto.UpdateTokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "헬스체크", description = "서버가 정상적으로 동작하는지 확인하는 API입니다.")
    @GetMapping("/health-check")
    public ApiResponse healthCheck() {
        return new ApiResponse(200, true, "서버가 정상적으로 동작 중입니다.", null);
    }

    @Operation(summary = "팀 변경 시 => JWT 토큰 갱신 -> 성공 시 Header Authorization 확인해주세요")
    @PutMapping("/{team}")
    public ApiResponse<UpdateTokenResponse> updateToken(@RequestAttribute("userId") int userId, @RequestAttribute("teamId") int teamId,
                                                        @PathVariable int team, HttpServletRequest request) {

        log.info("토큰 갱신 요청이 들어왔습니다." + userId + "가 " + teamId + "팀에서 " + team + "팀으로 이동");

        String jwtToken = request.getHeader("Authorization").substring(7);

        String newJwtToken = authService.updateToken(userId, teamId, team, jwtToken);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + newJwtToken);

        return new ApiResponse<>(
                200,
                true,
                "jwt 토큰이 새롭게 발급되었습니다.",
                UpdateTokenResponse.builder().jwtToken(newJwtToken).build());
    }
}
