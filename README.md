# Jit Source distributed version control system

This repo contains a simple Java-based version of git.
It basically offers git functionality to a limited extent.

## Features
1) initiate a new repository <p>
2) add (and remove) files to(from) the staging area <p>
3) commit added files <p>
4) check out former commits <p>

## How it works

- The Jit class can be called with optinal parameters <p>
- The class itself is stateless -> staging area is serialized <p>
- staging area builds a merkle tree <p>
- Hash nodes are computed from the content of its children <p>

## Examples

java Path.Jit init <p>
java Path.Jit add path/to/file <p>
java Path.Jit remove path/to/file <p>
java Path.Jit commit "message" <p>
java Path.jit checkout b5502597b61425d278f8aeac87e51a671a99e58a <p>

## JUnit testing

This project offers some JUnit tests. For testing purpose
, there are some dummy classes. 

<img src="res/workspaceScreenshot.jpg" alt="Workspace" width="250"/>

Based on those classes, the workflow can be tested. This includes checking, <p>
wether the hash values have changed correctly.

## Contributing

If you'd like to contribute, please fork the repository and use a feature
branch. Pull requests are warmly welcome.

## Licensing

This project is licensed under GNU General Public License v3.0.