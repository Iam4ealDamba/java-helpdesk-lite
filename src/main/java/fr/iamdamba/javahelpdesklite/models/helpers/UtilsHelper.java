package fr.iamdamba.javahelpdesklite.models.helpers;

import fr.iamdamba.javahelpdesklite.entities.MemberEntity;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class UtilsHelper {
    public MemberEntity getAuthenticatedMember() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (MemberEntity) authentication.getPrincipal();
    }
}
