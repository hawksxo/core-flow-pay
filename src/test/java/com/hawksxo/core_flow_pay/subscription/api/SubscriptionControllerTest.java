package com.hawksxo.core_flow_pay.subscription.api;

import com.hawksxo.core_flow_pay.subscription.application.ActivateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CancelSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionCommand;
import com.hawksxo.core_flow_pay.subscription.application.CreateSubscriptionUseCase;
import com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.subscription.domain.InvalidSubscriptionStateTransitionException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionAlreadyActiveException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SubscriptionController.class)
class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateSubscriptionUseCase createUseCase;

    @MockitoBean
    private ActivateSubscriptionUseCase activateUseCase;

    @MockitoBean
    private CancelSubscriptionUseCase cancelUseCase;

    private static final Instant NOW = Instant.parse("2026-01-01T00:00:00Z");

    @Test
    void create_success_returns201() throws Exception {
        var result = new CreateSubscriptionUseCase.SubscriptionResult("sub-1", "user-1", "plan-1", SubscriptionStatus.PENDING, "idem-1", NOW, null);
        when(createUseCase.execute(any(CreateSubscriptionCommand.class))).thenReturn(result);

        mockMvc.perform(post("/api/v1/subscriptions")
                .header("Idempotency-Key", "idem-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"user-1\", \"planId\": \"plan-1\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("sub-1"))
                .andExpect(jsonPath("$.userId").value("user-1"))
                .andExpect(jsonPath("$.planId").value("plan-1"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void create_missingIdempotencyKeyHeader_returns400() throws Exception {
        mockMvc.perform(post("/api/v1/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"user-1\", \"planId\": \"plan-1\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void create_duplicateIdempotencyKey_returns409() throws Exception {
        when(createUseCase.execute(any(CreateSubscriptionCommand.class)))
                .thenThrow(new DuplicateIdempotencyKeyException("idem-1"));

        mockMvc.perform(post("/api/v1/subscriptions")
                .header("Idempotency-Key", "idem-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"user-1\", \"planId\": \"plan-1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void create_userAlreadyActive_returns409() throws Exception {
        when(createUseCase.execute(any(CreateSubscriptionCommand.class)))
                .thenThrow(new SubscriptionAlreadyActiveException("user-1"));

        mockMvc.perform(post("/api/v1/subscriptions")
                .header("Idempotency-Key", "idem-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"userId\": \"user-1\", \"planId\": \"plan-1\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void activate_success_returns200() throws Exception {
        var result = new ActivateSubscriptionUseCase.SubscriptionResult("sub-1", "user-1", "plan-1", SubscriptionStatus.ACTIVE, "idem-1", NOW, NOW.plusSeconds(30L * 24 * 60 * 60));
        when(activateUseCase.execute("sub-1")).thenReturn(result);

        mockMvc.perform(post("/api/v1/subscriptions/sub-1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sub-1"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void activate_notFound_returns404() throws Exception {
        when(activateUseCase.execute("sub-1"))
                .thenThrow(new SubscriptionNotFoundException("sub-1"));

        mockMvc.perform(post("/api/v1/subscriptions/sub-1/activate"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void activate_wrongState_returns409() throws Exception {
        when(activateUseCase.execute("sub-1"))
                .thenThrow(new InvalidSubscriptionStateTransitionException("sub-1", SubscriptionStatus.ACTIVE, SubscriptionStatus.ACTIVE));

        mockMvc.perform(post("/api/v1/subscriptions/sub-1/activate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void cancel_success_returns200() throws Exception {
        var result = new CancelSubscriptionUseCase.SubscriptionResult("sub-1", "user-1", "plan-1", SubscriptionStatus.CANCELLED, "idem-1", NOW, null);
        when(cancelUseCase.execute("sub-1")).thenReturn(result);

        mockMvc.perform(post("/api/v1/subscriptions/sub-1/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("sub-1"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancel_notFound_returns404() throws Exception {
        when(cancelUseCase.execute("sub-1"))
                .thenThrow(new SubscriptionNotFoundException("sub-1"));

        mockMvc.perform(post("/api/v1/subscriptions/sub-1/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}