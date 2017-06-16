package com.example.ip;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class IpServiceTest {

    final static String IP = "8.8.8.8";

    @Mock
    IpInfoProxy primatyProxy, fallbackProxy;

    @Mock
    ClientResponse response;

    @Rule public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void testGetIpInfoWithCircuitBreakerClosed() {
        CircuitBreaker circuitBreaker = circuitBreaker();
        circuitBreaker.transitionToClosedState();
        IpService service = new IpService(primatyProxy, fallbackProxy, circuitBreaker);
        when(primatyProxy.requestIpInfo(IP)).thenReturn(Mono.just(response));
        service.getIpInfo(IP);
        verify(primatyProxy, times(1)).requestIpInfo(IP);
        verify(fallbackProxy, never()).requestIpInfo(IP);
    }

    private CircuitBreaker circuitBreaker() {
        return CircuitBreaker.ofDefaults("test");
    }

    @Test
    public void testGetIpInfoWithCircuitBreakerOpen() {
        CircuitBreaker circuitBreaker = circuitBreaker();
        circuitBreaker.transitionToOpenState();
        IpService service = new IpService(primatyProxy, fallbackProxy, circuitBreaker);
        when(fallbackProxy.requestIpInfo(IP)).thenReturn(Mono.just(response));
        service.getIpInfo(IP);
        verify(primatyProxy, never()).requestIpInfo(IP);
        verify(fallbackProxy, times(1)).requestIpInfo(IP);
    }

    @Test
    public void testGetIpInfoFailedPrimarySource() {
        CircuitBreaker circuitBreaker = circuitBreaker();
        circuitBreaker.transitionToClosedState();
        IpService service = new IpService(primatyProxy, fallbackProxy, circuitBreaker);
        when(primatyProxy.requestIpInfo(IP)).thenThrow(IllegalStateException.class);
        when(fallbackProxy.requestIpInfo(IP)).thenReturn(Mono.just(response));
        service.getIpInfo(IP);
        verify(primatyProxy, times(1)).requestIpInfo(IP);
        verify(fallbackProxy, times(1)).requestIpInfo(IP);
    }

}
