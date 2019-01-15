package client;

import java.io.IOException;

/**
 *
 * @author julian
 */
public class IORuntimeException extends RuntimeException {

    public IORuntimeException(IOException ex) {
        super(ex);
    }
    
}
