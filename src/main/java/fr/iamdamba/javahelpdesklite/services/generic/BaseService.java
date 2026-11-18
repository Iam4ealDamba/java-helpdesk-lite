package fr.iamdamba.javahelpdesklite.services.generic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.AccessDeniedException;

import java.util.List;

public interface BaseService<UUID, Dto, Create, Update> {
    ResponseEntity<List<Dto>> all() throws org.springframework.security.access.AccessDeniedException;

    ResponseEntity<Page<Dto>> search(Pageable pageable) throws AccessDeniedException;

    ResponseEntity<Create> create(Create create) throws java.nio.file.AccessDeniedException;

    ResponseEntity<Update> update(UUID id, Update update) throws java.nio.file.AccessDeniedException;

    ResponseEntity<Void> delete(UUID id) throws java.nio.file.AccessDeniedException;
}
