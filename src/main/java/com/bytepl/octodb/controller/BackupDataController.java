package com.bytepl.octodb.controller;

import com.bytepl.octodb.util.Zipper;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RequestMapping("/backup")
@RestController
public class BackupDataController {
    @Value("${octa-db.path.name}")
    private String path;

    @Value("${octa-db.path.backup}")
    private String path_backup;

    @GetMapping("/database/{databaseName}")
    public ResponseEntity dowloadDump(
            @PathVariable String databaseName
    ) throws IOException {
        File fileDir = new File("db/" +databaseName + "/");
        File fileOut = new File(path_backup + databaseName + new SimpleDateFormat("yyyyMMddmmss").format(new Date()) + ".zip");

        Zipper z = new Zipper(fileOut);
        z.zip(fileDir);
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Content-Type", "application/octet-stream");
        headers.add("Content-Disposition", "attachment; filename=\"zipFile.zip\"");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(fileOut));
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @PostMapping(path = "/database", consumes = {
            MediaType.MULTIPART_FORM_DATA_VALUE
    })
    public ResponseEntity loadDump(@RequestPart("file") MultipartFile multipartFile)
            throws IOException {
        //Solucion faltante...
        if (multipartFile.getContentType() == "application/x-zip-compressed"){
            File zip = File.createTempFile(UUID.randomUUID().toString(), "temp");
            FileOutputStream o = new FileOutputStream(zip);
            IOUtils.copy(multipartFile.getInputStream(), o);
            o.close();

            String destination = "";
            try {
                ZipFile zipFile = new ZipFile(zip);
                zipFile.extractAll(destination);
            } catch (ZipException e) {
                e.printStackTrace();
            } finally {
                zip.delete();
            }
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

}
