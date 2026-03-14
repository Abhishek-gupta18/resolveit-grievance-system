package com.example.demo.repository;

import com.example.demo.model.MediaUpload;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaUploadRepository extends JpaRepository<MediaUpload, Long> {
}
