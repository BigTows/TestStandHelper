package ru.uniteller.teststandhelper;

public class MapCommand {
    private String nameCommand;
    private PhpClassAndMethod phpClassAndMethod;

    public MapCommand(String nameCommand, PhpClassAndMethod phpClassAndMethod) {
        this.nameCommand = nameCommand;
        this.phpClassAndMethod = phpClassAndMethod;
    }


    public String getNameCommand() {
        return nameCommand;
    }

    public PhpClassAndMethod getPhpClassAndMethod() {
        return phpClassAndMethod;
    }
}
