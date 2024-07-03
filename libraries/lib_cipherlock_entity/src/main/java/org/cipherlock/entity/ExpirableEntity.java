package org.cipherlock.entity;

import lombok.Data;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@MappedSuperclass
@Data
public class ExpirableEntity extends AuditEntity {
    @Column(name = "effective_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime effectiveTimestamp;

    @Column(name = "expired_timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime expirationTimestamp;
}
