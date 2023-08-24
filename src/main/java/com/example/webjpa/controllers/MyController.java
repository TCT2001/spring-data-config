package com.example.webjpa.controllers;

import com.example.webjpa.configs.UserEntityManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.hibernate.Session;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MyController {
    private final UserEntityManagerFactory emfUser;
    String DO = "http://localhost:8080";
    private final String PROXY_PATH = "/1";
    private final ObjectMapper mapper = new ObjectMapper();

    @PutMapping(PROXY_PATH)
    public void proxyFile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = getBody(request);
        URL url = new URL("http://localhost:8080" + PROXY_PATH);
        String rs = doProxy(url, json, HttpMethod.POST, MediaType.APPLICATION_JSON);
        ObjectNode inputJsonObject = mapper.readValue(json, ObjectNode.class);
        ObjectNode rsObject = mapper.readValue(rs, ObjectNode.class);
        //JSONObject rsObject = new JSONObject(rs);

        boolean success = inputJsonObject.get("type").asLong() == 2
                && rsObject.get("name").asText().equals("TCT");
        if (success) {
            Long orderId = inputJsonObject.get("orderId").asLong();
            byte[] a = writeDataToFile(orderId);
            response.getOutputStream().write(a, 0, a.length);
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentLength(a.length);
        } else {
            response.getOutputStream().print("????");
            response.setContentType("application/json");
        }
    }

    @PostMapping(PROXY_PATH)
    public void proxyJson(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = getBody(request);
        URL url = new URL("http://localhost:8080" + PROXY_PATH);
        String rs = doProxy(url, json, HttpMethod.POST, MediaType.APPLICATION_JSON);
        ObjectNode inputJsonObject = mapper.readValue(json, ObjectNode.class);
        ObjectNode rsObject = mapper.readValue(rs, ObjectNode.class);

        boolean success = inputJsonObject.get("type").asLong() == 2
                && rsObject.get("name").asText().equals("TCT");
        if (success) {
            Long orderId = inputJsonObject.get("orderId").asLong();
            ObjectNode root = writeToJson(orderId);
            String res = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(root);
            response.getOutputStream().print(res);
            response.setContentType("application/json");
        } else {
            response.getOutputStream().print("????");
            response.setContentType("application/json");
        }
    }

    private String getBody(HttpServletRequest request) throws IOException {
        return new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String doProxy(URL url, String data, HttpMethod httpMethod, MediaType mediaType) throws IOException {
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(httpMethod.toString());
        con.setRequestProperty("Content-Type", mediaType.toString());
        con.setRequestProperty("Accept", mediaType.toString());
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return new String(con.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private byte[] writeDataToFile(Long orderId) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Workbook wb = new Workbook(os, "MyApplication", "1.0");
        EntityManager entityManager = emfUser.getObject().createEntityManager();
        Session session = entityManager.unwrap(Session.class);
        try {

            Worksheet ws = wb.newWorksheet("TCT2001");

            session.doWork(connection -> {
                int startRow = 0;
                ws.value(startRow, 0, "RequestCa");
                startRow += 1;

                writeRecords(connection, "SELECT * FROM users WHERE ID = 1", ws, startRow);

                startRow += 3;

                ws.value(startRow, 0, "SubCa");
                startRow += 1;
                writeRecords(connection, "SELECT * FROM users WHERE ID = 1", ws, startRow);
                startRow += 3;

                ws.value(startRow, 0, "SendEmail");
                startRow += 1;
                writeRecords(connection, "SELECT * FROM users WHERE ID = 1", ws, startRow);
                startRow += 2;

            });
            return os.toByteArray();
        } finally {
            try {
                wb.finish();
                wb.close();
                entityManager.clear();
                entityManager.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void writeRecords(Connection connection, String statement, Worksheet ws, int startRow) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        ResultSet rs = preparedStatement.executeQuery();
        ResultSetMetaData metadata = rs.getMetaData();

        if (!rs.isBeforeFirst()) {
            ws.value(startRow, 1, "NO DATA");
        } else {
            rs.next();
            for (int i = 0; i < metadata.getColumnCount(); i++) {
                ws.value(startRow, i, metadata.getColumnLabel(i + 1));
                ws.value(startRow + 1, i, rs.getString(metadata.getColumnLabel(i + 1)));
            }
        }
    }

    private MyData writeRecords(Connection connection, String statement, ObjectNode node, Aggregate aggregate) throws SQLException, InterruptedException {
        PreparedStatement preparedStatement = connection.prepareStatement(statement);
        ResultSet rs = preparedStatement.executeQuery();
        ResultSetMetaData metadata = rs.getMetaData();

        if (!rs.isBeforeFirst()) {
            node.put("ERROR", "NO DATA");
            return new MyData.MyDataBuilder()
                    .rs(false)
                    .build();
        } else {
            rs.next();

            //if (Aggregate.REQUEST_CA.equals(aggregate)) {
            //    for (int i = 0; i < 45; ++i) {
            //        if (rs.getLong("STATUS") == 6) {
            //            break;
            //        }
            //        Thread.sleep(2000);
            //        rs = preparedStatement.executeQuery();
            //        metadata = rs.getMetaData();
            //    }
            //}

            for (int i = 0; i < metadata.getColumnCount(); i++) {
                node.put(metadata.getColumnLabel(i + 1), rs.getString(metadata.getColumnLabel(i + 1)));
            }
        }
        if (Aggregate.REQUEST_CA.equals(aggregate)) {
            return new MyData.MyDataBuilder()
                    .rs(true)
                    .requestId(rs.getLong("REQUEST_ID"))
                    .subId(rs.getLong("SUB_ID"))
                    .build();
        } else if (Aggregate.SUB_CA.equals(aggregate)) {
            return new MyData.MyDataBuilder()
                    .rs(true)
                    .subId(rs.getLong("SUB_ID"))
                    .customerId(rs.getLong("CUST_ID"))
                    .build();
        }
        return new MyData.MyDataBuilder()
                .rs(true).build();
    }

    private ObjectNode writeToJson(Long orderId) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();

        EntityManager entityManager = emfUser.getObject().createEntityManager();
        Session session = entityManager.unwrap(Session.class);
        ObjectNode requestCaNode = mapper.createObjectNode();
        ObjectNode subCaNode = mapper.createObjectNode();
        ObjectNode sendEmailNode = mapper.createObjectNode();
        rootNode.set("requestCa", requestCaNode);
        rootNode.set("subCa", subCaNode);
        rootNode.set("sendEmail", sendEmailNode);
        AtomicReference<String> statement = new AtomicReference<>("SELECT * FROM users WHERE ID = 1");
        session.doWork(connection -> {
            try {
                //statement.set("SELECT * FROM REQUEST_CA WHERE BUNDLE_TRANS_ID = " + orderId);
                MyData requestCa = writeRecords(connection, statement.get(), requestCaNode, Aggregate.REQUEST_CA);

                //statement.set("SELECT * FROM SUB_CA WHERE SUB_ID = " + requestCa.subId());
                MyData subCa = writeRecords(connection, statement.get(), subCaNode, Aggregate.SUB_CA);

                //statement.set("SELECT * FROM CUSTOMER_CA WHERE CUST_ID = " + subCa.customerId());
                writeRecords(connection, statement.get(), sendEmailNode, Aggregate.SEND_EMAIL);

                //statement.set("SELECT * FROM SEND_EMAIL WHERE REQUEST_CA_ID = " + requestCa.requestId());
                writeRecords(connection, statement.get(), sendEmailNode, Aggregate.SEND_EMAIL);
            } catch (Exception e) {

            }

        });

        entityManager.clear();
        entityManager.close();

        return rootNode;
    }

    public enum Aggregate {
        REQUEST_CA,
        SUB_CA,
        SEND_EMAIL,
        CUSTOMER_CA,
        CUSTOMER_LEGAL,
        CA_MESSAGE
    }
}

@Builder
record MyData(boolean rs, Long requestId, Long subId, Long customerId) {
}
