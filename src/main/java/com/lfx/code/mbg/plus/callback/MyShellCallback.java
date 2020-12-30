package com.lfx.code.mbg.plus.callback;

import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;

import java.io.File;
import java.util.StringTokenizer;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

/**
 * @author <a href="mailto:idler41@163.com">idler41</a>
 * @date 2020-04-12 19:31:14
 */
public class MyShellCallback extends DefaultShellCallback {
    /**
     * Instantiates a new default shell callback.
     *
     * @param overwrite the overwrite
     */
    public MyShellCallback(boolean overwrite) {
        super(overwrite);
    }

//    @Override
//    public File getDirectory(String targetProject, String targetPackage) throws ShellException {
//        // targetProject is interpreted as a directory that must exist
//        //
//        // targetPackage is interpreted as a sub directory, but in package
//        // format (with dots instead of slashes). The sub directory will be
//        // created
//        // if it does not already exist
//
//        File project = new File(targetProject);
//
//        // lfx自定义: 自动创建目标目录
//        if (!project.exists()) {
//            project.mkdirs();
//        }
//        if (!project.isDirectory()) {
//            throw new ShellException(getString("Warning.9", //$NON-NLS-1$
//                    targetProject));
//        }
//
//        StringBuilder sb = new StringBuilder();
//        StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
//        while (st.hasMoreTokens()) {
//            sb.append(st.nextToken());
//            sb.append(File.separatorChar);
//        }
//
//        File directory = new File(project, sb.toString());
//        if (!directory.isDirectory()) {
//            boolean rc = directory.mkdirs();
//            if (!rc) {
//                throw new ShellException(getString("Warning.10", //$NON-NLS-1$
//                        directory.getAbsolutePath()));
//            }
//        }
//
//        return directory;
//    }
}
