package me.kirok.restapi.configs;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;
import me.kirok.restapi.accounts.Account;
import me.kirok.restapi.accounts.AccountRole;
import me.kirok.restapi.accounts.AccountService;
import me.kirok.restapi.common.BaseControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class AuthServerConfigTest extends BaseControllerTest {


    @Autowired
    AccountService accountService;

    @Test
    @DisplayName("인증 토큰 발급 테스트")
    public void getAuthToken() throws Exception {
        //given

        String username = "keeun@email.com";
        String password = "keesun";
        Account account = Account.builder()
            .email(username)
            .password(password)
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();

        this.accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        this.mockMvc.perform(
            post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password")
        )
            .andExpect(status().isOk())
            .andExpect(jsonPath("access_token").exists());


    }


}