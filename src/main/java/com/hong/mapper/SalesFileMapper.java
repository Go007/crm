package com.hong.mapper;

import com.hong.pojo.SalesFile;

import java.util.List;

public interface SalesFileMapper {
    List<SalesFile> findBySalesId(Integer salesId);
    void save(SalesFile salesFile);
    SalesFile findById(Integer id);

    void del(List<SalesFile> salesFileList);
}
