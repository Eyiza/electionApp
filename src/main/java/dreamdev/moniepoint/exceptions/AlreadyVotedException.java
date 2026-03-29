package dreamdev.moniepoint.exceptions;

public class AlreadyVotedException extends ElectionAppException {
    public AlreadyVotedException(String message) {
        super(message);
    }
}
