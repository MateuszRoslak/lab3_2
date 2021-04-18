package edu.iis.mto.time;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@ExtendWith(MockitoExtension.class)
class OrderTest {

    @Mock
    private Clock clock;
    private Order order;
    private Instant submitDate;

    @BeforeEach
    void setUp() throws Exception {
        order = new Order(clock);
        submitDate = Instant.parse("2020-05-04T00:00:00Z");
    }

    @Test
    public void OrderExpiredWithoutCatch() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submitDate).thenReturn(submitDate.plusSeconds(86400));

        try {
            order.submit();
            order.confirm();
        } catch (OrderExpiredException ignored) {
            fail("failed");
        }
    }

    @Test
    public void OrderExpiredWithCatch() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submitDate).thenReturn(submitDate.plusSeconds(90000));

        try {
            order.submit();
            order.confirm();
            fail("failed");
        } catch (OrderExpiredException ignored) {
        }
    }

    @Test
    public void OrderExpiredWithoutCatchingState() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submitDate).thenReturn(submitDate.plusSeconds(86400));

        try {
            order.submit();
            order.confirm();

        } catch (OrderExpiredException ignored) {
            fail("failed");
        }
        Order.State orderState = order.getOrderState();
        assertEquals(orderState, Order.State.CONFIRMED);
    }

    @Test
    public void OrderExpiredWithCatchingState() {
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());
        when(clock.instant()).thenReturn(submitDate).thenReturn(submitDate.plusSeconds(90000));

        try {
            order.submit();
            order.confirm();
        } catch (OrderExpiredException ignored) {
        }

        Order.State orderState = order.getOrderState();
        assertEquals(orderState, Order.State.CANCELLED);
    }
}
