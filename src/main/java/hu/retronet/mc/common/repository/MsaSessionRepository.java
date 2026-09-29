package hu.retronet.mc.common.repository;

import hu.retronet.mc.common.entity.GameSession;
import hu.retronet.mc.common.entity.MsaSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MsaSessionRepository extends JpaRepository<MsaSession, Long> {
}
