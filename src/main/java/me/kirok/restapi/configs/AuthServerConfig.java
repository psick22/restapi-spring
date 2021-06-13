package me.kirok.restapi.configs;

import me.kirok.restapi.accounts.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final AccountService accountService;

    private final TokenStore tokenStore;

    public AuthServerConfig(PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager, AccountService accountService,
        TokenStore tokenStore) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
        this.tokenStore = tokenStore;
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
            .allowFormAuthenticationForClients()
            .passwordEncoder(passwordEncoder);
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients
            .inMemory()
            .withClient("myApp")
            .secret(this.passwordEncoder.encode("pass"))
            .scopes("all")
            .authorizedGrantTypes("password", "refresh_token")
            .authorities("ROLE_ADMIN")
            .accessTokenValiditySeconds(10 * 60)
            .refreshTokenValiditySeconds(6 * 10 * 60)
            .autoApprove(true);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
            .authenticationManager(authenticationManager)
            .userDetailsService(accountService)
            .tokenStore(tokenStore);

    }
}
