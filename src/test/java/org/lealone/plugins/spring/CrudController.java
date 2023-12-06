/*
 * Copyright Lealone Database Group.
 * Licensed under the Server Side Public License, v 1.
 * Initial Developer: zhh
 */
package org.lealone.plugins.spring;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CrudController {

    private static Connection getConnection() throws SQLException {
        // 通过嵌入模式访问lealone数据库
        return DriverManager.getConnection("jdbc:lealone:embed:lealone", "root", "");
    }

    private final Connection conn;
    private final PreparedStatement insert;
    private final PreparedStatement find;

    public CrudController() throws SQLException {
        conn = getConnection();
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS user");
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user (name varchar primary key, age int)");
        stmt.close();
        insert = conn.prepareStatement("INSERT INTO user(name, age) VALUES(?, ?)");
        find = conn.prepareStatement("SELECT age FROM user WHERE name = ?");
    }

    // 打开url: http://localhost:8080/insert?name=zhh&age=18
    @GetMapping("/insert")
    public String insert(@RequestParam(value = "name", defaultValue = "zhh") String name,
            @RequestParam("age") int age) {
        try {
            insert.setString(1, name);
            insert.setInt(2, age);
            insert.executeUpdate();
            return "insert ok";
        } catch (SQLException e) {
            return "insert exception: " + e.getMessage();
        }
    }

    // 打开url: http://localhost:8080/find?name=zhh
    @GetMapping("/find")
    public String find(@RequestParam("name") String name) {
        try {
            find.setString(1, name);
            ResultSet rs = find.executeQuery();
            if (rs.next()) {
                return "user(name: " + name + ", age: " + rs.getInt(1) + ")";
            } else {
                return "user not found: " + name;
            }
        } catch (SQLException e) {
            return "exception: " + e.getMessage();
        }
    }
}
