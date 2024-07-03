package org.cipherlock.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.OffsetDateTime;

@MappedSuperclass
@Data
@EntityListeners(value = AuditingEntityListener.class)
public class AuditEntity implements Serializable {

    @Column(name = "created_timestamp", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @CreatedDate
    private OffsetDateTime createTimestamp;

    @Column(name = "last_updated_timestamp", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    @JsonIgnore
    @LastModifiedDate
    private OffsetDateTime lastUpdateTimestamp;

    @Column(name = "updated_by", nullable = false)
    @JsonIgnore
    @LastModifiedBy
    private String updatedBy;

    @PrePersist
    public void prePersist() {
        setUpdatedBy("CIPHERLOCK");
    }

}
