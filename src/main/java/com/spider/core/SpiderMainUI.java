package com.spider.core;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;

/**
 * @author shiliang.li
 * @date 2020/5/15
 */
public class SpiderMainUI {
    public static final String SEACH_URL = "/w?q={skuCode}&vst={skuCode}";

    public void initJframe() {
        JFrame jFrame = new JFrame();
        jFrame.setName("主界面");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLocation(500, 500);
        jFrame.setSize(600, 300);
        jFrame.setResizable(false);
        ImageIcon imageIcon = new ImageIcon(getClass().getResource("/image/icon_1.png"));
        jFrame.setIconImage(imageIcon.getImage());
        JPanel panel = new JPanel();
        jFrame.add(panel);
        placeComponents(panel);
        jFrame.setVisible(true);
    }


    private void placeComponents(JPanel panel) {
        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);


        JLabel nikeUrl = new JLabel("nikeUrl");
        nikeUrl.setBounds(50,10,80,25);
        panel.add(nikeUrl);
        JTextField nikeUrlText = new JTextField();
        nikeUrlText.setBounds(150,10,300,25);
        panel.add(nikeUrlText);

        // 创建 skuCodeJLabel
        JLabel skuCodeLabel = new JLabel("skuCode:");
        skuCodeLabel.setBounds(50,40,80,25);
        panel.add(skuCodeLabel);
        JTextField skuCodeText = new JTextField();
        skuCodeText.setBounds(150,40,300,25);
        panel.add(skuCodeText);

        // pdpurl
        JLabel pdpUrlLabel = new JLabel("pdpUrl:");
        pdpUrlLabel.setBounds(50,70,80,25);
        panel.add(pdpUrlLabel);
        JTextField pdpUrlText = new JTextField();
        pdpUrlText.setBounds(150,70,300,25);
        panel.add(pdpUrlText);

        // cssStyle
        JLabel cssStyleLabel = new JLabel("cssStyle:");
        cssStyleLabel.setBounds(50,100,80,25);
        panel.add(cssStyleLabel);
        JTextField cssStyleText = new JTextField();
        cssStyleText.setBounds(150,100,300,25);
        cssStyleText.setText("css-1bhwifz");
        panel.add(cssStyleText);

        JLabel chromeDriverLabel = new JLabel("chromeDriver:");
        chromeDriverLabel.setBounds(20,130,100,25);
        panel.add(chromeDriverLabel);
        JTextField chromeDriverText = new JTextField();
        chromeDriverText.setBounds(150,130,300,25);
        panel.add(chromeDriverText);

        JButton chromeDriverBtn = new JButton("打开");
        chromeDriverBtn.setBounds(460, 130, 80, 25);
        chromeDriverBtn.addActionListener(e -> {
            //文件下载保存地址
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            Component component = null;
            int saveDialog = jFileChooser.showSaveDialog(component);
            if (saveDialog == JFileChooser.APPROVE_OPTION) {
                String selectPath = jFileChooser.getSelectedFile().getPath();
                chromeDriverText.setText(selectPath);
            }
        });
        panel.add(chromeDriverBtn);

        // 抓取
        JButton spiderBtn = new JButton("抓取");
        spiderBtn.setBounds(220, 170, 80, 25);
        spiderBtn.addActionListener(e -> {
            //文件下载保存地址
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            Component component = null;
            int saveDialog = jFileChooser.showSaveDialog(component);
            if (saveDialog == JFileChooser.APPROVE_OPTION) {
                // 获得路径
                String selectPath = jFileChooser.getSelectedFile().getPath() +"\\";
                System.out.println("你选择的目录是：" + selectPath);

                System.out.println(skuCodeText.getText() + "|" + pdpUrlText.getText() + " | " + cssStyleText.getText());
                SpiderCommand spiderCommand = new SpiderCommand();
                spiderCommand.setSkuCode(skuCodeText.getText());
                if (StringUtils.isNotBlank(skuCodeText.getText())) {
                    spiderCommand.setMasterStationUrl(nikeUrlText.getText()
                            + StringUtils.replaceEachRepeatedly(SEACH_URL, new String[]{"{skuCode}", "{skuCode}"},
                            new String[]{skuCodeText.getText(), skuCodeText.getText()}));
                } else {
                    if (StringUtils.isNotBlank(pdpUrlText.getText())) {
                        spiderCommand.setMasterStationUrl(pdpUrlText.getText());
                        spiderCommand.setPdpUrl(pdpUrlText.getText());
                    }
                }
                spiderCommand.setCssStyle(cssStyleText.getText());
                spiderCommand.setExportPath(selectPath);
                spiderCommand.setChromDriverPath(chromeDriverText.getText());
                try {
                    SpiderUtils.downZip(spiderCommand);
                    JOptionPane.showMessageDialog(null, "下载成功！","", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "下载失败！","", JOptionPane.ERROR_MESSAGE);
                }

            }


        });
        panel.add(spiderBtn);
    }





    public static void main(String[] args) {
        SpiderMainUI spiderMainUI = new SpiderMainUI();
        spiderMainUI.initJframe();
    }

}
