package com.hong.mapper;

import com.hong.pojo.Notice;

import java.util.List;
import java.util.Map;

public interface NoticeMapper {
    void save(Notice notice);

    List<Notice> findByParam(Map<String, Object> param);

    Long count();

    Notice findById(Integer id);
}
