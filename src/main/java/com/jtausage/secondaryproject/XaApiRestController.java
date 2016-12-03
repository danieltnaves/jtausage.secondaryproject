package com.jtausage.secondaryproject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by danielnaves on 03/12/16.
 */
@RestController
public class XaApiRestController {

    private final JdbcTemplate a, b;

    public XaApiRestController(DataSource a, DataSource b) {
        this.a = new JdbcTemplate(a);
        this.b = new JdbcTemplate(b);
    }

    @GetMapping("/pets")
    public Collection<String> pets() {
        return this.a.query("SELECT * FROM PET", (resultSet, i) -> resultSet.getString("NICKNAME"));
    }

    @GetMapping("/messages")
    public Collection<String> messages() {
        return this.b.query("SELECT * FROM MESSAGE", (resultSet, i) -> resultSet.getString("MESSAGE"));
    }

    @PostMapping
    @Transactional
    public void write(@RequestBody Map<String, String> payload, @RequestParam Optional<Boolean> rollback) {
        String name = payload.get("name");
        String msg = "Hello, " + name + " !";
        this.a.update("INSERT INTO PET (ID, NICKNAME) VALUES (?, ?)", UUID.randomUUID().toString(), name);
        this.b.update("INSERT INTO MESSAGE (ID, MESSAGE) VALUES (?, ?)", UUID.randomUUID().toString(), msg);
        if (rollback.orElse(false)) {
            throw new RuntimeException("Não foi possível inserir dados nas bases de dados.");
        }
    }

}
