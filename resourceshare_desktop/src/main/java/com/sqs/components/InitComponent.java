package com.sqs.components;

/**
 * @author sqs
 * @date 2024-08
 * @description
 * @since
 */
public interface InitComponent
{
    default void initialize()
    {
        initComponents();
        initView();
        setListeners();
    }

    void initComponents();

    void initView();

    void setListeners();
}
