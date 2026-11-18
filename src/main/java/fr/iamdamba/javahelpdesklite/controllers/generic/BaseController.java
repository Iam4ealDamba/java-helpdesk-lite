package fr.iamdamba.javahelpdesklite.controllers.generic;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface BaseController<UUID, Dto, Create, Update> {

    ResponseEntity<List<Dto>> all() throws AccessDeniedException;

    ResponseEntity<Page<Dto>> search(@PageableDefault(size = 10) Pageable pageable) throws AccessDeniedException;

    ResponseEntity<Create> create(Create create) throws AccessDeniedException;

    ResponseEntity<Update> update(UUID id, Update update) throws AccessDeniedException;

    ResponseEntity<Void> delete(UUID id) throws AccessDeniedException;
}
