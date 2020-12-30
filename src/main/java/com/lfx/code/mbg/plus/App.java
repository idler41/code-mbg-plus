package com.lfx.code.mbg.plus;

import com.lfx.code.mbg.plus.callback.MyShellCallback;
import com.lfx.code.mbg.plus.context.PlusContext;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.MyBatisGenerator;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 10:29:40
 */
@Slf4j
public class App {

    public static void main(String[] args) throws Exception {
        log.info("代码生成器运行开始...");
        PlusContext.initContext();
        MyBatisGenerator myBatisGenerator = new MyBatisGenerator(PlusContext.CONFIG, new MyShellCallback(true), PlusContext.WARNINGS);
        myBatisGenerator.generate(null);
        log.info("代码生成器运行结束...");
    }
}


