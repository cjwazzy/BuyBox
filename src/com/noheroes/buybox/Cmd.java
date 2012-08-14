/*
 * Copyright (C) 2011 No Heroes.
 * See readme for license details.
 */
package com.noheroes.buybox;

import com.noheroes.buybox.Exceptions.InsufficientPermissionException;
import com.noheroes.buybox.Exceptions.MissingOrIncorrectArgumentException;

/**
 * @author Sorklin <sorklin at gmail.com>
 */
public interface Cmd {
    public boolean execute() 
            throws MissingOrIncorrectArgumentException, InsufficientPermissionException;
}
