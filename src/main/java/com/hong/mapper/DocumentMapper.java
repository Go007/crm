package com.hong.mapper;

import com.hong.pojo.Document;

import java.util.List;

public interface DocumentMapper {

    void save(Document document);

    List<Document> findByFid(Integer fid);

    Document findById(Integer id);

    void del(Integer id);

}
