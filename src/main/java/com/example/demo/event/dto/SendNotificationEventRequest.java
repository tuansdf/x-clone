package com.example.demo.event.dto;

import com.example.demo.common.dto.RequestContext;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class SendNotificationEventRequest implements Serializable {

    private RequestContext requestContext;
    private UUID notificationId;

}
