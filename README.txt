SCTags will parse scala source code and write a ctags-compatible tags file.

= Usage =

Windows:
  sctags.bat [-f file] [-R] <files>

Unix:
  sctags.sh [-f file] [-R] <files>

-f file
  sets the file the tags are written to, default is "tags"
  (Use "-" for standard output)

-R
  recurse through directories specified on the command line
