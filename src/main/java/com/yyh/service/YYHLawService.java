package com.yyh.service;

import com.yyh.domain.*;
import com.yyh.repository.LawRepository;
import com.yyh.repository.search.LawSearchRepository;
import com.yyh.security.AuthoritiesConstants;
import com.yyh.service.util.ExcelLoadUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Law.
 */
@Service
@Transactional
public class YYHLawService {

    private final Logger log = LoggerFactory.getLogger(LawService.class);

    @Inject
    private LawRepository lawRepository;

    @Inject
    private LawSearchRepository lawSearchRepository;

    /**
     * Import a laws
     *
     * @param filepath the file to convert
     * @return the law list
     */
    public List<Law> importLaws(String filepath) {
        List<Law> laws = new ArrayList<>();
        /**
         * 第一部分：找到文件，加载EXCEL文件到内存
         */
        Workbook workbook = ExcelLoadUtil.loadExcel(filepath);
        /**
         * 第二部分：加载相关对象到内存中，以键值对存储，降低时间复杂度
         */
        List<Law> lawList = lawRepository.findAll();
        Map<String, Law> lawMap = new HashMap<>();
        for (Law law : lawList) {
            lawMap.put(law.getLawTitle(), law);
        }
        /**
         * 第三部分：读取并解析Excel表格
         */
        Sheet sheet = workbook.getSheetAt(0);
        for (int i = 0; i < sheet.getLastRowNum(); i = i + 1) {
            if (i > 0) {
                try {
                    Row row = sheet.getRow(i);
                    String lawTitleOrigin = row.getCell(1).getStringCellValue();
                    String pattern = "《(.*)》";
                    Pattern r = Pattern.compile(pattern);
                    String lawTitle = r.matcher(lawTitleOrigin).group();
                    /**
                     * 排除名称一样的法律
                     */
                    if (lawMap.get(lawTitle) != null) {
                        continue;
                    } else {
                        Law law = new Law();
                        law.setLawTitle(lawTitle);
                        laws.add(law);
                    }
                } catch (IllegalStateException ex) {
                    ex.printStackTrace();
                }
            }
        }
        List<Law> lawArrayList = new ArrayList<>();
        if (laws.size() > 0) {
            lawArrayList = lawRepository.save(laws);
            lawSearchRepository.save(laws);
        }
        return lawArrayList;
    }

}
