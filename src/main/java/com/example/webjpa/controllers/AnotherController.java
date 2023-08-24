package com.example.webjpa.controllers;

import com.example.webjpa.configs.UserEntityManagerFactory;
import com.example.webjpa.entities.db1.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.hibernate.Session;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Date;

@RestController
@RequestMapping("/1")
@Slf4j
@RequiredArgsConstructor
public class AnotherController {
    private final UserEntityManagerFactory emfUser;

    @GetMapping
    public String test1() {
        try (EntityManager entityManager = emfUser.getObject().createEntityManager()) {
            entityManager.getTransaction().begin();

            User user = new User("TCT", 21L, new Date());
            entityManager.persist(user);

            entityManager.getTransaction().commit();

        //    entityManager.getTransaction().begin();
        //
        //    Session session = entityManager.unwrap(Session.class);
        //
        //    ObjectMapper mapper = new ObjectMapper();
        //    ObjectNode rootNode = mapper.createObjectNode();
        //    ObjectNode requestCa = mapper.createObjectNode();
        //
        //    session.doWork(connection -> {
        //        PreparedStatement preparedStatement = connection.prepareStatement("""
        //                            SELECT * FROM users WHERE ID = 1
        //                """
        //        );
        //        ResultSet rs = preparedStatement.executeQuery();
        //        ResultSetMetaData metadata = rs.getMetaData();
        //
        //        if (!rs.isBeforeFirst()) {
        //            System.out.println("No data");
        //            return;
        //        }
        //        rs.next();
        //
        //        for (int i = 0; i < metadata.getColumnCount(); i++) {
        //            requestCa.put(metadata.getColumnLabel(i + 1), rs.getString(metadata.getColumnLabel(i + 1)));
        //        }
        //    });
        //
        //    rootNode.set("requestCa", requestCa);
        //    String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        //    System.out.println(jsonString);
        //} catch (JsonProcessingException e) {
        //    throw new RuntimeException(e);
        }
        return "1";
    }


    @PostMapping("/2")
    public void test2(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        //String command = "curl -X POST https://postman-echo.com/post --data foo1=bar1&foo2=bar2";
        String json = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(json);
        URL url = new URL("http://localhost:8080/1");
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        try(OutputStream os = con.getOutputStream()) {
            byte[] input = json.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder responsePost = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                responsePost.append(responseLine.trim());
            }
            response.getOutputStream().print(responsePost.toString());
        }
        response.setContentType("application/json");
    }

    @PostMapping
    public String hello() {
        return "{\"name\": \"TCT\"}";
    }

    @PostMapping(path = "/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Resource file() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        try (Workbook wb = new Workbook(os, "MyApplication", "1.0")) {
            Worksheet ws = wb.newWorksheet("Sheet 1");
            ws.width(0, 25);
            ws.width(1, 15);

            ws.range(0, 0, 0, 1).style().fontName("Arial").fontSize(16).bold().fillColor("3366FF").set();
            ws.value(0, 0, "Name");
            ws.value(0, 1, "Age");

            ws.range(2, 0, 2, 1).style().wrapText(true).set();
            ws.value(2, 0, "John Smith");
            ws.value(2, 1, 20L);
        }
        ByteArrayResource resource = new ByteArrayResource(os.toByteArray());
        return resource;
    }


    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        ObjectNode childNode1 = mapper.createObjectNode();
        childNode1.put("name1", "val1");
        childNode1.put("name2", "val2");

        rootNode.set("obj1", childNode1);

        ObjectNode childNode2 = mapper.createObjectNode();
        childNode2.put("name3", "val3");
        childNode2.put("name4", "val4");

        rootNode.set("obj2", childNode2);

        ObjectNode childNode3 = mapper.createObjectNode();
        childNode3.put("name5", "val5");
        childNode3.put("name6", "val6");

        rootNode.set("obj3", childNode3);


        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        System.out.println(jsonString);
    }

}
