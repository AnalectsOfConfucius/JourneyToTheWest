package com.yyh.service;

import com.yyh.JourneyToTheWestApp;
import com.yyh.domain.Law;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JourneyToTheWestApp.class)
@Transactional
public class YYHLawServiceIntTest {

    @Inject
    private YYHLawService YYHLawService;

    @Test
    public void assertThatImportLawService() {
        /*List<Manager> managerList = YYHManagerService.importManagers("E://万宁市工商局执法检查人员名录库.xls");
        assertThat(managerList.size() > 0);*/
        List<Law> lawList = YYHLawService.importLaws("/home/yuyuhui/YYH/工商常用法律法规.xls");
        assertThat(lawList.size()).isGreaterThan(0);
    }

}
