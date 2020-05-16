package com.spider.core;

import org.apache.commons.lang3.StringUtils;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Component;

/**
 * @author shiliang.li
 * @date 2020/5/15
 */
public class SpiderMainUI {
    public static final String SEACH_URL = "https://www.nike.com/cn/w?q={skuCode}&vst={skuCode}";

    public void initJframe() {
        JFrame jFrame = new JFrame();
        jFrame.setName("主界面");
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocation(500, 500);
        jFrame.setSize(700, 500);
        JPanel panel = new JPanel();
        jFrame.add(panel);
        placeComponents(panel);
    }


    private void placeComponents(JPanel panel) {
        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 skuCodeJLabel
        JLabel skuCodeLabel = new JLabel("skuCode:");
        skuCodeLabel.setBackground(Color.BLUE);
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        skuCodeLabel.setBounds(10,20,80,25);
        panel.add(skuCodeLabel);
        JTextField skuCodeText = new JTextField();
        skuCodeText.setBounds(100,20,300,25);
        panel.add(skuCodeText);

        // pdpurl
        JLabel pdpUrlLabel = new JLabel("pdpUrl:");
        pdpUrlLabel.setBounds(10,50,80,25);
        panel.add(pdpUrlLabel);
        JTextField pdpUrlText = new JTextField();
        pdpUrlText.setBounds(100,50,300,25);
        panel.add(pdpUrlText);

        // cssStyle
        JLabel cssStyleLabel = new JLabel("cssStyle:");
        cssStyleLabel.setBounds(10,80,80,25);
        panel.add(cssStyleLabel);
        JTextField cssStyleText = new JTextField();
        cssStyleText.setBounds(100,80,300,25);
        cssStyleText.setText("css-1bhwifz");
        panel.add(cssStyleText);



        // 抓取
        JButton spiderBtn = new JButton("抓取");
        spiderBtn.setBounds(10, 100, 80, 25);
        spiderBtn.addActionListener(e -> {
            //文件下载保存地址
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Component component = null;
            int saveDialog = jFileChooser.showSaveDialog(component);
            if (saveDialog == JFileChooser.APPROVE_OPTION) {
                // 获得路径
                String selectPath = jFileChooser.getSelectedFile().getPath();
                System.out.println("你选择的目录是：" + selectPath);

                System.out.println(skuCodeText.getText() + "|" + pdpUrlText.getText() + " | " + cssStyleText.getText());
                SpiderCommand spiderCommand = new SpiderCommand();
                spiderCommand.setSkuCode(skuCodeText.getText());
                if (StringUtils.isNotBlank(skuCodeText.getText())) {
                    spiderCommand.setMasterStationUrl(StringUtils.replaceEachRepeatedly(SEACH_URL, new String[]{"{skuCode}","{skuCode}"}, new String[]{skuCodeText.getText(),skuCodeText.getText()}));
                } else {
                    if (StringUtils.isNotBlank(pdpUrlText.getText())) {
                        spiderCommand.setMasterStationUrl(pdpUrlText.getText());
                        spiderCommand.setPdpUrl(pdpUrlText.getText());
                    }
                }
                spiderCommand.setCssStyle(cssStyleText.getText());
                spiderCommand.setExportPath(selectPath);
                SpiderUtils.downZip(spiderCommand);

            }


        });
        panel.add(spiderBtn);
    }





    public static void main(String[] args) {
        SpiderMainUI spiderMainUI = new SpiderMainUI();
        spiderMainUI.initJframe();
    }

}
